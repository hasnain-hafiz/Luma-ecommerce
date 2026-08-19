import { Toaster } from "@/components/ui/sonner";
import { TooltipProvider } from "@/components/ui/tooltip";
import NotFound from "@/pages/NotFound";
import { Route, Switch } from "wouter";
import ErrorBoundary from "./components/ErrorBoundary";
import { ThemeProvider } from "./contexts/ThemeContext";
import Home from "./pages/Home";

function Router() {
  return (
    <Switch>
      <Route path="/" component={Home} />
      <Route path="/shop" component={Home} />
      <Route path="/product/:slug" component={Home} />
      <Route path="/assistant" component={Home} />
      <Route path="/login" component={Home} />
      <Route path="/register" component={Home} />
      <Route path="/forgot-password" component={Home} />
      <Route path="/checkout" component={Home} />
      <Route path="/checkout/confirmation" component={Home} />
      <Route path="/checkout/confirmation/:id" component={Home} />
      <Route path="/order/:id" component={Home} />
      <Route path="/account/:section" component={Home} />
      <Route path="/admin/:section" component={Home} />
      <Route path="/404" component={NotFound} />
      <Route component={NotFound} />
    </Switch>
  );
}

export default function App() {
  return (
    <ErrorBoundary>
      <ThemeProvider defaultTheme="light">
        <TooltipProvider>
          <Toaster />
          <Router />
        </TooltipProvider>
      </ThemeProvider>
    </ErrorBoundary>
  );
}
