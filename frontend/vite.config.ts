import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import viteTsconfigPaths from 'vite-tsconfig-paths';
import eslint from 'vite-plugin-eslint';

export default defineConfig(({ mode }) => ({
  // depending on your application, base can also be "/"
  base: mode === 'production' ? '/dailygrind/' : '/',
  plugins: [react(), viteTsconfigPaths(), eslint()],
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          // Splits these large libraries into separate chunks
          react: ['react', 'react-dom'],
          mui: ['@mui/material', '@emotion/react', '@emotion/styled'],
          amplify: ['aws-amplify', '@aws-amplify/ui-react'],
        },
      },
    },
  },
  server: {
    open: true,  // this ensures that the browser opens upon server start
    port: 3000,
    allowedHosts: ['frontend'],
  },
}));
