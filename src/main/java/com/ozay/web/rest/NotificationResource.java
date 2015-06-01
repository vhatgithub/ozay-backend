package com.ozay.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ozay.domain.Notification;
import com.ozay.domain.User;
import com.ozay.repository.BuildingRepository;
import com.ozay.repository.NotificationRepository;
import com.ozay.repository.UserRepository;
import com.ozay.security.AuthoritiesConstants;
import com.ozay.security.SecurityUtils;
import com.ozay.service.MailService;
import com.ozay.service.NotificationService;
import com.ozay.web.rest.dto.JsonResponse;
import com.ozay.web.rest.dto.NotificationDTO;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Notification.
 */
@RestController
@RequestMapping("/api")
public class NotificationResource {

    private final Logger log = LoggerFactory.getLogger(NotificationResource.class);

    @Inject
    private NotificationRepository notificationRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private NotificationService notificationService;

    @Inject
    private BuildingRepository buildingRepository;

    /**
     * POST  /notifications -> Create a new notification.
     */
    @RequestMapping(value = "/notifications",
        method = RequestMethod.POST,
        consumes = "application/json",
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JsonResponse> create(@RequestBody NotificationDTO notificationDto) {
        log.debug("REST request to save Notification : {}", notificationDto);
        int emailCount = notificationService.sendNotice(notificationDto);

        JsonResponse json = new JsonResponse();

        String message = "Notice is successfully scheduled to " + emailCount + " recipients";
        json.setResponse(message);

        return new ResponseEntity<JsonResponse>(json,  new HttpHeaders(), HttpStatus.OK);
    }

    /**
     * GET  /notifications -> get all the notifications.
     */
    @RequestMapping(value = "/notifications",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @RolesAllowed(AuthoritiesConstants.ADMIN)
    public List<Notification> getAll() {
        log.debug("REST request to get all Notifications");
        return notificationRepository.findAll();
    }

    /**
     * GET  /notifications -> get all the notifications.
     */
    @RequestMapping(value = "/notifications/building/{buildingId}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Notification> getAllByBuilding(@PathVariable Integer buildingId) {
        log.debug("REST request to get all Notifications by Building");
        return notificationRepository.findAllByBuilding(buildingId);
    }

    /**
     * GET  /notifications/:id -> get the "id" notification.
     */
    @RequestMapping(value = "/notifications/notification/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Notification> get(@PathVariable Long id) {
        log.debug("REST request to get Notification : {}", id);
        return Optional.ofNullable(notificationRepository.findOne(id))
            .map(notification -> new ResponseEntity<>(
                notification,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /notifications/:id -> delete the "id" notification.
     */
    @RequestMapping(value = "/notifications/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Notification : {}", id);
        notificationRepository.delete(id);
    }
}
