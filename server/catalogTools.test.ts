import { describe, expect, it } from "vitest";

const catalog = [
  { slug: "orbit-runner", name: "Orbit Runner", category: "Movement", price: 148 },
  { slug: "daylight-pack", name: "Daylight Pack", category: "Carry", price: 125 },
];

const searchProducts = (query: string) => catalog.filter(product => `${product.name} ${product.category}`.toLowerCase().includes(query.toLowerCase()));
const getProductsByCategory = (category: string) => catalog.filter(product => product.category.toLowerCase() === category.toLowerCase());

// Contract-level smoke tests for the controlled catalog tool boundary.
describe("controlled catalog tools", () => {
  it("searches only returned catalog records", () => {
    expect(searchProducts("runner").map(product => product.slug)).toEqual(["orbit-runner"]);
    expect(searchProducts("unknown")).toEqual([]);
  });

  it("filters categories without inventing records", () => {
    expect(getProductsByCategory("Carry")).toEqual([{ slug: "daylight-pack", name: "Daylight Pack", category: "Carry", price: 125 }]);
  });
});
