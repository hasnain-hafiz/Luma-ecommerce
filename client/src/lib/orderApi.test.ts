import { describe, expect, it, vi } from "vitest";
import { classifyOrderApiError, orderApi } from "./orderApi";

describe("order API reliability", () => {
  it("classifies unauthorized and forbidden responses as authentication failures", () => {
    expect(classifyOrderApiError(401)).toBe("AUTH_REQUIRED");
    expect(classifyOrderApiError(403)).toBe("AUTH_REQUIRED");
  });

  it("classifies transient and server failures as retryable request failures", () => {
    expect(classifyOrderApiError(408)).toBe("ORDER_REQUEST_FAILED");
    expect(classifyOrderApiError(429)).toBe("ORDER_REQUEST_FAILED");
    expect(classifyOrderApiError(500)).toBe("ORDER_REQUEST_FAILED");
  });

  it("forwards the session bearer token while retaining cookie credentials", async () => {
    const fetchMock = vi.fn().mockResolvedValue(new Response(JSON.stringify({ ok: true }), { status: 200 }));
    vi.stubGlobal("fetch", fetchMock);
    vi.stubGlobal("window", { sessionStorage: { getItem: (key: string) => key === "manus-cookie" ? "app_session_id=access-token" : null } });
    await orderApi<{ ok: boolean }>("/orders");
    expect(fetchMock).toHaveBeenCalledWith(expect.stringContaining("/api/v1/orders"), expect.objectContaining({ credentials: "include", headers: expect.objectContaining({ Authorization: "Bearer access-token" }) }));
    vi.unstubAllGlobals();
  });

  it("surfaces 401 as an auth-required error for retry UI", async () => {
    vi.stubGlobal("fetch", vi.fn().mockResolvedValue(new Response("", { status: 401 })));
    await expect(orderApi("/orders")).rejects.toThrow("AUTH_REQUIRED");
    vi.unstubAllGlobals();
  });
});
