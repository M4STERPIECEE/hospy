package com.rdv.booking.service;

import com.rdv.booking.dto.PublicBookingRequest;
import com.rdv.booking.dto.PublicBookingResponse;

public interface PublicBookingService {
    PublicBookingResponse book(PublicBookingRequest request);
}
