package com.baber.saloonservice.repository;

import com.baber.saloonservice.model.Saloon;
import com.baber.saloonservice.model.StaffInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StaffInvitationRepository extends JpaRepository<StaffInvitation, Long> {
    
    Optional<StaffInvitation> findByInvitationToken(UUID invitationToken);
    
    List<StaffInvitation> findBySalon(Saloon salon);
    
    @Query("SELECT si FROM StaffInvitation si WHERE si.salon.id = :salonId ORDER BY si.createdAt DESC")
    List<StaffInvitation> findBySalonIdOrderByCreatedAtDesc(@Param("salonId") Long salonId);
    
    @Query("SELECT si FROM StaffInvitation si WHERE si.salon.id = :salonId AND si.status = :status")
    List<StaffInvitation> findBySalonIdAndStatus(@Param("salonId") Long salonId, @Param("status") String status);
    
    Optional<StaffInvitation> findBySalonAndEmailAndStatus(Saloon salon, String email, String status);
}
