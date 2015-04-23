package com.ozay.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ozay.domain.Notification;
import com.ozay.domain.User;
import com.ozay.repository.NotificationRepository;
import com.ozay.repository.UserRepository;
import com.ozay.security.SecurityUtils;
import com.ozay.service.MailService;
import com.sendgrid.Mail;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    private MailService mailService;
    /**
     * POST  /notifications -> Create a new notification.
     */
    @RequestMapping(value = "/notifications",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Notification notification) {
        User currentUser = userRepository.findOne(SecurityUtils.getCurrentLogin());
        notification.setCreatedBy(currentUser.getLogin());
        notification.setCreatedDate(new DateTime());
        notification.setBuildingId(1);
        mailService.sendGrid("Notification", notification.getNotice());
        log.debug("REST request to save Notification : {}", notification);
        notificationRepository.save(notification);
    }

    /**
     * GET  /notifications -> get all the notifications.
     */
    @RequestMapping(value = "/notifications",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Notification> getAll() {
        log.debug("REST request to get all Notifications");
        return notificationRepository.findAll();
    }

    /**
     * GET  /notifications/:id -> get the "id" notification.
     */
    @RequestMapping(value = "/notifications/{id}",
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