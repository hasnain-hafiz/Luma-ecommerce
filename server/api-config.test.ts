import { describe, expect, it } from "vitest";

describe("Java API configuration", () => {
  it("has the supplied database and JWT configuration present without exposing values", () => {
    expect(process.env.LUMA_API_DATABASE_URL).toMatch(/^jdbc:postgresql:\/\//);
    expect(process.env.LUMA_API_DB_USERNAME).toBeTruthy();
    expect(process.env.LUMA_API_DB_PASSWORD).toBeTruthy();
    expect(process.env.LUMA_API_JWT_SECRET).toBeTruthy();
    expect(process.env.LUMA_API_DATABASE_URL).not.toContain(process.env.LUMA_API_DB_PASSWORD ?? "__missing__");
  });
});
