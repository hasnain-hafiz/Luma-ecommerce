package com.luma.commerce.catalog;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "brands")
public class BrandEntity {
  @Id private UUID id;
  private String slug;
  private String name;
  protected BrandEntity() {}
  public UUID getId() { return id; }
  public String getSlug() { return slug; }
  public String getName() { return name; }
}
