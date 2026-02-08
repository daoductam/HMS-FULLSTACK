/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{js,jsx,ts,tsx}"],
  theme: {
    extend: {
      colors: {
        dark: "#212529",
        light: "#F0F3FB",
        // fontFamily: {
        //   body: "Poppins, sans-serif",
        //   sans: "Poppins, sans-serif",
        // },
        primary: [
          "#f1fcfa",
          "#cff8ef",
          "#9ff0e1",
          "#67e1cf",
          "#32b9a9",
          "#1fad9f",
          "#168b82",
          "#166f69",
          "#165955",
          "#174a47",
          "#072c2b",
        ],

        neutral: [
          "#f6f6f6",
          "#e7e7e7",
          "#d1d1d1",
          "#b0b0b0",
          "#888888",
          "#6d6d6d",
          "#5d5d5d",
          "#4f4f4f",
          "#454545",
          "#3d3d3d",
          "#000000",
        ],
      },
    },
  },
  plugins: [],
};
