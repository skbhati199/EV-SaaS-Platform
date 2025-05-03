package com.ev.roamingservice.ocpi.module.credentials.service.impl;

import com.ev.roamingservice.model.OcpiParty;
import com.ev.roamingservice.model.OcpiToken;
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

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;
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
        token.setTokenType("A");
        token.setTokenA(credentials.getToken());
        token.setTokenB(generateToken());
        token.setTokenC(null);
        token.setValidUntil(ZonedDateTime.now().plusMonths(3));
        token.setStatus("ACTIVE");
        
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
        Optional<OcpiToken> tokenOpt = tokenRepository.findByTokenA(currentToken);
        
        if (!tokenOpt.isPresent()) {
            logger.error("Token not found in database: {}", currentToken);
            throw new IllegalArgumentException("Invalid token");
        }
        
        OcpiToken token = tokenOpt.get();
        OcpiParty party = token.getParty();
        
        // Update party details
        party.setPartyId(credentials.getPartyId());
        party.setCountryCode(credentials.getCountryCode());
        party.setRole(credentials.getRoles().get(0).getRole());
        party.setName(credentials.getBusinessDetails().getName());
        party.setWebsite(credentials.getBusinessDetails().getWebsite());
        party.setLogoUrl(credentials.getBusinessDetails().getLogo() != null ? credentials.getBusinessDetails().getLogo().getUrl() : null);
        
        // Update token
        token.setTokenA(credentials.getToken());
        token.setTokenC(token.getTokenB());
        token.setTokenB(generateToken());
        token.setValidUntil(ZonedDateTime.now().plusMonths(3));
        
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
        Optional<OcpiToken> tokenOpt = tokenRepository.findByTokenA(currentToken);
        
        if (!tokenOpt.isPresent()) {
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
        Optional<OcpiToken> tokenOpt = tokenRepository.findByTokenA(token);
        
        if (!tokenOpt.isPresent()) {
            logger.error("Token not found in database: {}", token);
            return false;
        }
        
        OcpiToken ocpiToken = tokenOpt.get();
        
        // Check if the token is expired
        if (ocpiToken.getValidUntil() != null && ocpiToken.getValidUntil().isBefore(ZonedDateTime.now())) {
            logger.error("Token expired: {}", token);
            return false;
        }
        
        // Check if the token is active
        if (!"ACTIVE".equals(ocpiToken.getStatus())) {
            logger.error("Token not active: {}", token);
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
        }
        
        party.setRole(credentials.getRoles().get(0).getRole());
        party.setName(credentials.getBusinessDetails().getName());
        party.setWebsite(credentials.getBusinessDetails().getWebsite());
        party.setLogoUrl(credentials.getBusinessDetails().getLogo() != null ? credentials.getBusinessDetails().getLogo().getUrl() : null);
        
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