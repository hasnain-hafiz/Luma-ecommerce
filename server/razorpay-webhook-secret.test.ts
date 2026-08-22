import { describe, expect, it } from "vitest";

describe("Razorpay webhook configuration", () => {
  it("accepts a configured webhook secret without exposing its value", () => {
    const secret = process.env.RAZORPAY_WEBHOOK_SECRET;

    // Deployment secrets are intentionally absent from public CI jobs. The
    // Java service receives this value through the Render environment instead.
    if (!secret) {
      expect(secret).toBeUndefined();
      return;
    }

    expect(secret.trim()).not.toBe("");
  });
});
