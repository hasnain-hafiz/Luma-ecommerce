import { describe, expect, it, vi } from "vitest";

describe("Razorpay webhook configuration", () => {
  it("uses the configured secret in a lightweight health request without exposing it", async () => {
    const secret = process.env.RAZORPAY_WEBHOOK_SECRET;
    expect(secret).toBeTruthy();

    const fetchMock = vi.fn().mockResolvedValue({ ok: true, status: 200 });
    const healthUrl = "https://luma-ecommerce.onrender.com/actuator/health";
    await fetchMock(healthUrl, {
      method: "GET",
      headers: { "X-Razorpay-Webhook-Secret": secret },
    });

    expect(fetchMock).toHaveBeenCalledWith(healthUrl, expect.objectContaining({
      headers: { "X-Razorpay-Webhook-Secret": secret },
    }));
    const forwardedHeaders = fetchMock.mock.calls[0]?.[1]?.headers as Record<string, string>;
    expect(forwardedHeaders["X-Razorpay-Webhook-Secret"]).toBe(secret);
  });
});
