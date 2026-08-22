import { authenticatedHeaders, orderApi } from "./orderApi";

export type ProductSummary = {
  id: string;
  slug: string;
  sku: string;
  name: string;
  brand: string;
  category: string;
  priceCents: number;
  compareAtCents: number | null;
  ratingAverage: number;
  ratingCount: number;
  inventoryQuantity: number;
  available: boolean;
  imageUrl: string | null;
};

export type ProductDetail = ProductSummary & {
  description: string;
  imageUrls: string[];
};

export type ProductPage = {
  items: ProductSummary[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
};

export type CartLine = {
  id: string;
  productId: string;
  productName: string;
  sku: string;
  unitPriceCents: number;
  quantity: number;
  lineTotalCents: number;
  available: boolean;
};

export type CartView = {
  id: string;
  items: CartLine[];
  subtotalCents: number;
  validationRequired: boolean;
};

export const commerceApi = {
  searchProducts: (params: URLSearchParams = new URLSearchParams()) =>
    orderApi<ProductPage>(`/catalog${params.toString() ? `?${params.toString()}` : ""}`),
  getProduct: (slug: string) => orderApi<ProductDetail>(`/catalog/${encodeURIComponent(slug)}`),
  getCart: () => orderApi<CartView>("/cart"),
  addCartItem: (productId: string, quantity: number) =>
    post<CartView>("/cart/items", { productId, quantity }),
  updateCartItem: (productId: string, quantity: number) =>
    patch<CartView>(`/cart/items/${encodeURIComponent(productId)}`, { quantity }),
  removeCartItem: (productId: string) =>
    deleteRequest<void>(`/cart/items/${encodeURIComponent(productId)}`),
};

async function post<T>(path: string, body: unknown): Promise<T> {
  return request<T>(path, { method: "POST", body: JSON.stringify(body) });
}

async function patch<T>(path: string, body: unknown): Promise<T> {
  return request<T>(path, { method: "PATCH", body: JSON.stringify(body) });
}

async function deleteRequest<T>(path: string): Promise<T> {
  return request<T>(path, { method: "DELETE" });
}

async function request<T>(path: string, init: RequestInit): Promise<T> {
  const base = import.meta.env.VITE_ORDER_API_BASE_URL || "";
  const response = await fetch(`${base}/api/v1${path}`, {
    ...init,
    credentials: "include",
    headers: { ...authenticatedHeaders(true), ...(init.headers || {}) },
  });
  if (!response.ok) throw new Error(response.status === 401 || response.status === 403 ? "AUTH_REQUIRED" : "COMMERCE_REQUEST_FAILED");
  if (response.status === 204) return undefined as T;
  return response.json() as Promise<T>;
}
