package com.ev.roamingservice.ocpi.module.credentials.service.impl;

import com.ev.roamingservice.model.OcpiConnectionStatus;
import com.ev.roamingservice.model.OcpiParty;
import com.ev.roamingservice.model.OcpiToken;
import com.ev.roamingservice.model.OcpiTokenType;
import com.ev.roamingservice.ocpi.module.credentials.dto.Credentials;
import com.ev.roamingservice.ocpi.module.credentials.service.CredentialsService;
import com.ev.roamingservice.repository.OcpiPartyRepository;
import com.ev.roamingservice.repository.OcpiTokenRepository;
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
 * Implementation of the credentials service
 */
@Service
public class CredentialsServiceImpl implements CredentialsService {

    private static final Logger logger = LoggerFactory.getLogger(CredentialsServiceImpl.class);
    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Token ";

    private final OcpiPartyRepository partyRepository;
    private final OcpiTokenRepository tokenRepository;

    @Autowired
    public CredentialsServiceImpl(OcpiPartyRepository partyRepository, OcpiTokenRepository tokenRepository) {
        this.partyRepository = partyRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    @Transactional
    public void storeCredentials(Credentials credentials) {
        logger.debug("Storing credentials for party: {}", credentials.getPartyId());

        // Create or update the party
        OcpiParty party = findOrCreateParty(credentials);
        
        // Create the token
        OcpiToken token = new OcpiToken();
        token.setParty(party);
        token.setTokenType(OcpiTokenType.A);
        token.setToken(credentials.getToken());
        token.setValidUntil(LocalDateTime.now().plusMonths(3));
        token.setRevoked(false);
        
        tokenRepository.save(token);
        
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
        // Update other fields as needed
        
        // Update token
        token.setToken(credentials.getToken());
        token.setValidUntil(LocalDateTime.now().plusMonths(3));
        
        partyRepository.save(party);
        tokenRepository.save(token);
        
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
        
        // Delete the token
        tokenRepository.delete(token);
        
        logger.debug("Credentials deleted");
    }

    @Override
    public Credentials getCredentialsByToken(String token) {
        logger.debug("Getting credentials by token");
        
        // Not implemented yet - would convert from OcpiToken to Credentials DTO
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        logger.debug("Validating token: {}", token);
        
        // Find the token in the database
        Optional<OcpiToken> tokenOpt = tokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            logger.error("Token not found in database: {}", token);
            return false;
        }
        
        OcpiToken ocpiToken = tokenOpt.get();
        
        // Check if the token is expired
        if (ocpiToken.getValidUntil() != null && ocpiToken.getValidUntil().isBefore(LocalDateTime.now())) {
            logger.error("Token expired: {}", token);
            return false;
        }
        
        // Check if the token is revoked
        if (ocpiToken.isRevoked()) {
            logger.error("Token revoked: {}", token);
            return false;
        }
        
        logger.debug("Token validated: {}", token);
        return true;
    }

    /**
     * Extract the token from the Authorization header
     * @return the token or null if not found
     */
    private String extractTokenFromHeader() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        
        if (requestAttributes == null) {
            return null;
        }
        
        HttpServletRequest request = requestAttributes.getRequest();
        String authHeader = request.getHeader(TOKEN_HEADER);
        
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            return null;
        }
        
        return authHeader.substring(TOKEN_PREFIX.length());
    }

    /**
     * Find or create a party based on the credentials
     * @param credentials the credentials
     * @return the party
     */
    private OcpiParty findOrCreateParty(Credentials credentials) {
        Optional<OcpiParty> partyOpt = partyRepository.findByPartyIdAndCountryCode(
                credentials.getPartyId(), credentials.getCountryCode());
        
        OcpiParty party;
        
        if (partyOpt.isPresent()) {
            party = partyOpt.get();
        } else {
            party = new OcpiParty();
            party.setPartyId(credentials.getPartyId());
            party.setCountryCode(credentials.getCountryCode());
            party.setStatus(OcpiConnectionStatus.PENDING);
        }
        
        // Set other fields as needed from credentials
        
        return partyRepository.save(party);
    }

    /**
     * Generate a random token
     * @return a UUID as a string
     */
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
} 