import { COOKIE_NAME } from "@shared/const";

export type OrderApiErrorCode = "AUTH_REQUIRED" | "ORDER_REQUEST_FAILED";

export function classifyOrderApiError(status: number): OrderApiErrorCode {
  return status === 401 || status === 403 ? "AUTH_REQUIRED" : "ORDER_REQUEST_FAILED";
}

function readAccessToken(): string | null {
  if (typeof window === "undefined" || !window.sessionStorage) return null;
  const explicit = window.sessionStorage.getItem("luma-access-token");
  if (explicit) return explicit;
  const rawCookie = window.sessionStorage.getItem("manus-cookie");
  const prefix = `${COOKIE_NAME}=`;
  return rawCookie?.split(";").find(part => part.trim().startsWith(prefix))?.trim().slice(prefix.length) ?? null;
}

export async function orderApi<T>(path: string): Promise<T> {
  const base = import.meta.env.VITE_ORDER_API_BASE_URL || "";
  const headers: HeadersInit = { Accept: "application/json" };
  const accessToken = readAccessToken();
  if (accessToken) headers.Authorization = `Bearer ${accessToken}`;
  const response = await fetch(`${base}/api/v1${path}`, { credentials: "include", headers });
  if (!response.ok) throw new Error(classifyOrderApiError(response.status));
  return response.json() as Promise<T>;
}
