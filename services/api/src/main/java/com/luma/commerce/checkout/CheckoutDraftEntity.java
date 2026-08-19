package com.luma.commerce.checkout;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "checkout_drafts")
public class CheckoutDraftEntity {
  @Id private UUID id;
  private UUID userId;
  @Enumerated(EnumType.STRING) private DraftStatus status;
  private String currency;
  private int subtotalCents;
  private int shippingCents;
  private int taxCents;
  private int totalCents;
  private String shippingName;
  private String shippingLine1;
  private String shippingLine2;
  private String shippingCity;
  private String shippingRegion;
  private String shippingPostalCode;
  private String shippingCountry;
  private Instant expiresAt;
  private Instant createdAt;
  private Instant updatedAt;
  protected CheckoutDraftEntity() {}
  public static CheckoutDraftEntity open(UUID userId, CheckoutContracts.ShippingAddress shipping, CheckoutContracts.AuthoritativeTotal total, Instant expiresAt) { var draft = new CheckoutDraftEntity(); draft.id = UUID.randomUUID(); draft.userId = userId; draft.status = DraftStatus.OPEN; draft.currency = total.currency(); draft.subtotalCents = total.subtotalCents(); draft.shippingCents = total.shippingCents(); draft.taxCents = total.taxCents(); draft.totalCents = total.totalCents(); draft.shippingName = shipping.name(); draft.shippingLine1 = shipping.line1(); draft.shippingLine2 = shipping.line2(); draft.shippingCity = shipping.city(); draft.shippingRegion = shipping.region(); draft.shippingPostalCode = shipping.postalCode(); draft.shippingCountry = shipping.country(); draft.expiresAt = expiresAt; draft.createdAt = Instant.now(); draft.updatedAt = Instant.now(); return draft; }
  public UUID getId() { return id; }
  public UUID getUserId() { return userId; }
  public DraftStatus getStatus() { return status; }
  public int getSubtotalCents() { return subtotalCents; }
  public int getShippingCents() { return shippingCents; }
  public int getTaxCents() { return taxCents; }
  public String getCurrency() { return currency; }
  public CheckoutContracts.ShippingAddress shippingAddress() { return new CheckoutContracts.ShippingAddress(shippingName, shippingLine1, shippingLine2, shippingCity, shippingRegion, shippingPostalCode, shippingCountry); }
  public int getTotalCents() { return totalCents; }
  public boolean isOpen() { return status == DraftStatus.OPEN && expiresAt.isAfter(Instant.now()); }
  public void convert() { if (status != DraftStatus.OPEN) throw new IllegalStateException("Draft is not open"); status = DraftStatus.CONVERTED; updatedAt = Instant.now(); }
  public void expire() { if (status == DraftStatus.OPEN) { status = DraftStatus.EXPIRED; updatedAt = Instant.now(); } }
  public void cancel() { if (status != DraftStatus.OPEN) throw new IllegalStateException("Only open drafts can be cancelled"); status = DraftStatus.CANCELLED; updatedAt = Instant.now(); }
  public enum DraftStatus { OPEN, CONVERTED, EXPIRED, CANCELLED }
}
