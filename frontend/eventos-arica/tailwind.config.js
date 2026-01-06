/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        'arica-blue': {
          DEFAULT: '#0033A0',
          dark: '#002080',
          light: '#1a4db8',
        },
        'arica-cyan': '#A8E6E6',
        'arica-yellow': '#F5C842',
        'arica-orange': '#F58742',
        'arica-coral': '#F56B6B',
        'arica-green': '#42C895',
      },
    },
  },
  plugins: [],
}