package org.app.backend.modules.rental.dto;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RentalPreviewDTO {
  private String rentalId;
  private String readerName;
  private String readerPhone;
  private String subscriptionPackage;
  private Instant borrowDate;
  private Instant dueDate;
  private String bookTitle;
  private String bookAuthor;
  private String bookBarcode;
  private BigDecimal bookPrice;
  private boolean isOverdue;
  private long overdueDays;
  private Instant displayDateTime;
}
