"use client";

import { useTheme } from "next-themes";
import { useEffect } from "react";

/**
 * ThemeStyles - A component that applies theme-specific styling to improve dark mode
 * This component doesn't render anything, it just adds/removes CSS classes on theme change
 */
export function ThemeStyles() {
  const { theme } = useTheme();
  
  useEffect(() => {
    // Apply smooth scrolling and improved dark mode text rendering
    document.documentElement.style.colorScheme = theme === "dark" ? "dark" : "light";
    
    // Apply custom dark mode styles to handle images, charts, and other content
    if (theme === "dark") {
      document.documentElement.classList.add("theme-dark");
      
      // Fix image contrast in dark mode
      const style = document.createElement("style");
      style.id = "dark-image-fix";
      style.textContent = `
        .theme-dark img:not([src*=".svg"]) {
          filter: brightness(0.9) contrast(1.1);
        }
      `;
      document.head.appendChild(style);
    } else {
      document.documentElement.classList.remove("theme-dark");
      const style = document.getElementById("dark-image-fix");
      if (style) style.remove();
    }
  }, [theme]);
  
  return null;
} 