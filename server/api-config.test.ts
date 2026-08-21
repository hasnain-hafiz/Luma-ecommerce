import { describe, expect, it } from "vitest";

describe("Java API configuration", () => {
  it("validates configured database and JWT settings without exposing values", () => {
    const databaseUrl = process.env.LUMA_API_DATABASE_URL;
    const username = process.env.LUMA_API_DB_USERNAME;
    const password = process.env.LUMA_API_DB_PASSWORD;
    const jwtSecret = process.env.LUMA_API_JWT_SECRET;
    const configured = [databaseUrl, username, password, jwtSecret].some(Boolean);

    // GitHub Actions intentionally does not receive deployment secrets. The Java
    // deployment validates their presence at startup; this test validates them
    // when a configured environment is available without printing any value.
    if (!configured) return;

    expect(databaseUrl).toMatch(/^(jdbc:)?postgresql:\/\//);
    expect(username).toBeTruthy();
    expect(password).toBeTruthy();
    expect(jwtSecret).toBeTruthy();
    expect(databaseUrl).not.toContain(password ?? "__missing__");
  });
});
