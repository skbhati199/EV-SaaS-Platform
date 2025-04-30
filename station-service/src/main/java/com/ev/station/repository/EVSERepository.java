package com.ev.station.repository;

import com.ev.station.model.EVSE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EVSERepository extends JpaRepository<EVSE, UUID> {
    
    /**
     * Find an EVSE by its unique EVSE ID
     * @param evseId The EVSE ID
     * @return Optional containing the EVSE if found
     */
    Optional<EVSE> findByEvseId(String evseId);
    
    /**
     * Find all EVSEs owned by a specific user
     * @param ownerId The owner's ID
     * @return List of EVSEs owned by the user
     */
    List<EVSE> findByOwnerId(UUID ownerId);
    
    /**
     * Check if an EVSE with the given EVSE ID exists
     * @param evseId The EVSE ID to check
     * @return True if an EVSE with the given ID exists
     */
    boolean existsByEvseId(String evseId);
}
