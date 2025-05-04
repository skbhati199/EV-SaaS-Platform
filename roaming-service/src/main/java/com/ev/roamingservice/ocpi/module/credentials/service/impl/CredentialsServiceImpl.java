package com.ev.roamingservice.ocpi.module.credentials.service.impl;

import com.ev.roamingservice.model.OcpiConnectionStatus;
import com.ev.roamingservice.model.OcpiParty;
import com.ev.roamingservice.model.OcpiRole;
import com.ev.roamingservice.model.OcpiToken;
import com.ev.roamingservice.model.OcpiTokenType;
import com.ev.roamingservice.ocpi.module.credentials.dto.Credentials;
import com.ev.roamingservice.ocpi.module.credentials.dto.CredentialsRole;
import com.ev.roamingservice.ocpi.module.credentials.service.CredentialsService;
import com.ev.roamingservice.repository.OcpiPartyRepository;
import com.ev.roamingservice.repository.OcpiTokenRepository;
import com.ev.roamingservice.service.RoamingPartnerService;
import com.ev.roamingservice.service.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the credentials service using event-driven architecture
 */
@Service
public class CredentialsServiceImpl implements CredentialsService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialsServiceImpl.class);
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Token ";

    private final OcpiPartyRepository partyRepository;
    private final OcpiTokenRepository tokenRepository;
    private final RoamingPartnerService roamingPartnerService;
    private final TokenService tokenService;

    @Autowired
    public CredentialsServiceImpl(
            OcpiPartyRepository partyRepository, 
            OcpiTokenRepository tokenRepository,
            RoamingPartnerService roamingPartnerService,
            TokenService tokenService) {
        this.partyRepository = partyRepository;
        this.tokenRepository = tokenRepository;
        this.roamingPartnerService = roamingPartnerService;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public void storeCredentials(Credentials credentials) {
        logger.debug("Storing credentials for party: {}", credentials.getPartyId());

        // Create the party through the RoamingPartnerService to trigger events
        String partyId = credentials.getPartyId();
        String countryCode = credentials.getCountryCode();
        String name = credentials.getBusinessDetails() != null ? 
                credentials.getBusinessDetails().getName() : "Unknown Party";
        
        // Determine the role from credentials
        OcpiRole role = OcpiRole.OTHER;
        if (!credentials.getRoles().isEmpty()) {
            String roleStr = credentials.getRoles().get(0).getRole();
            if (roleStr != null) {
                try {
                    role = OcpiRole.valueOf(roleStr);
                } catch (IllegalArgumentException e) {
                    logger.warn("Unknown role: {}, defaulting to OTHER", roleStr);
                }
            }
        }
        
        // Register the partner using the service which will trigger events
        OcpiParty party = roamingPartnerService.registerPartner(
                countryCode, partyId, name, role, credentials.getUrl());
        
        // Create the token using TokenService which will trigger events
        tokenService.createToken(party, OcpiTokenType.A, 24 * 90); // 90 days
        
        logger.debug("Credentials stored for party: {}", credentials.getPartyId());
    }

    @Override
    @Transactional
    public void updateCredentials(Credentials credentials) {
        logger.debug("Updating credentials for party: {}", credentials.getPartyId());
        
        // Get current token from header
        String currentToken = extractTokenFromHeader();
        
        if (currentToken == null) {
            logger.error("No token found in header for credentials update");
            throw new IllegalArgumentException("No token found in header");
        }
        
        // Find the token in the database
        Optional<OcpiToken> tokenOpt = tokenRepository.findByToken(currentToken);
        
        if (tokenOpt.isEmpty()) {
            logger.error("Token not found in database: {}", currentToken);
            throw new IllegalArgumentException("Invalid token");
        }
        
        OcpiToken token = tokenOpt.get();
        OcpiParty party = token.getParty();
        
        // Update party details
        party.setPartyId(credentials.getPartyId());
        party.setCountryCode(credentials.getCountryCode());
        
        // Update name if available
        if (credentials.getBusinessDetails() != null && credentials.getBusinessDetails().getName() != null) {
            party.setName(credentials.getBusinessDetails().getName());
        }
        
        // Update URL if available
        if (credentials.getUrl() != null) {
            party.setVersionsUrl(credentials.getUrl());
        }
        
        // Save party to update fields in database
        party = partyRepository.save(party);
        
        // Create a new token using TokenService which will trigger events
        if (credentials.getToken() != null && !credentials.getToken().isEmpty()) {
            // First, revoke the old token
            tokenService.revokeToken(token);
            
            // Then create a new token
            token = tokenService.createToken(party, OcpiTokenType.C, 24 * 90); // 90 days
        } else {
            // Just refresh the existing token
            token = tokenService.refreshToken(token, 24 * 90); // 90 days
        }
        
        // If the party status is still pending, establish the connection
        if (party.getStatus() == OcpiConnectionStatus.PENDING) {
            roamingPartnerService.establishConnection(party);
        }
        
        logger.debug("Credentials updated for party: {}", credentials.getPartyId());
    }

    @Override
    @Transactional
    public void deleteCredentials() {
        logger.debug("Deleting credentials");
        
        // Get current token from header
        String currentToken = extractTokenFromHeader();
        
        if (currentToken == null) {
            logger.error("No token found in header for credentials deletion");
            throw new IllegalArgumentException("No token found in header");
        }
        
        // Find the token in the database
        Optional<OcpiToken> tokenOpt = tokenRepository.findByToken(currentToken);
        
        if (tokenOpt.isEmpty()) {
            logger.error("Token not found in database: {}", currentToken);
            throw new IllegalArgumentException("Invalid token");
        }
        
        OcpiToken token = tokenOpt.get();
        OcpiParty party = token.getParty();
        
        // First, revoke the token using TokenService
        tokenService.revokeToken(token);
        
        // Then disconnect the party
        roamingPartnerService.disconnectPartner(party);
        
        // Finally, delete the party
        roamingPartnerService.deletePartner(party);
        
        logger.debug("Credentials deleted");
    }

    @Override
    public Credentials getCredentialsByToken(String token) {
        logger.debug("Getting credentials by token");
        
        Optional<OcpiToken> tokenOpt = tokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            return null;
        }
        
        OcpiToken ocpiToken = tokenOpt.get();
        OcpiParty party = ocpiToken.getParty();
        
        if (party == null) {
            return null;
        }
        
        return mapPartyToCredentials(party, ocpiToken.getToken());
    }

    @Override
    public boolean validateToken(String token) {
        logger.debug("Validating token: {}", token);
        
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        // Use TokenService to validate the token
        return tokenService.validateToken(token);
    }

    /**
     * Extract the token from the Authorization header
     * @return the token or null if not found
     */
    private String extractTokenFromHeader() {
        try {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                String authHeader = request.getHeader(TOKEN_HEADER);
                
                if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
                    return authHeader.substring(TOKEN_PREFIX.length());
                }
            }
        } catch (Exception e) {
            logger.error("Error extracting token from header", e);
        }
        
        return null;
    }

    /**
     * Map from domain entity to DTO
     * @param party the OcpiParty entity
     * @param token the token to include
     * @return a Credentials DTO
     */
    private Credentials mapPartyToCredentials(OcpiParty party, String token) {
        CredentialsRole role = CredentialsRole.builder()
                .role(party.getRole().name())
                .countryCode(party.getCountryCode())
                .partyId(party.getPartyId())
                .build();
        
        return Credentials.builder()
                .token(token)
                .url(party.getVersionsUrl())
                .countryCode(party.getCountryCode())
                .partyId(party.getPartyId())
                .roles(java.util.Collections.singletonList(role))
                .build();
    }
} 