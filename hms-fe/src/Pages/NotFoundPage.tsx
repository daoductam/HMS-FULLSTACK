import React from "react";
import { Link, useNavigate } from "react-router-dom";

// 404Page.jsx
// Default export React component. Uses Tailwind CSS classes for styling.
// - Works with react-router (Link + useNavigate). If you don't use react-router,
//   the "Go home" button falls back to window.location = "/".
// - Fully responsive and accessible.

const NotFoundPage = ({ homePath = "/" }) => {
  const navigate = useNavigate?.() ?? null;

  const goHome = () => {
    if (navigate) navigate(homePath);
    else window.location.href = homePath;
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-sky-50 via-white to-rose-50 flex items-center justify-center p-6">
      <main
        className="max-w-4xl w-full bg-white/80 backdrop-blur-md rounded-3xl shadow-xl border border-gray-100 p-8 md:p-12 flex flex-col md:flex-row items-center gap-8"
        aria-labelledby="pageTitle"
        role="main"
      >
        {/* Left: Illustration */}
        <section className="flex-1 flex items-center justify-center">
          <div className="w-full max-w-sm">
            {/* Decorative SVG */}
            <svg
              viewBox="0 0 600 400"
              xmlns="http://www.w3.org/2000/svg"
              className="w-full h-auto"
              role="img"
              aria-hidden="true"
            >
              <defs>
                <linearGradient id="g1" x1="0" x2="1">
                  <stop offset="0%" stopColor="#60a5fa" />
                  <stop offset="100%" stopColor="#fb7185" />
                </linearGradient>
                <linearGradient id="g2" x1="0" x2="1">
                  <stop offset="0%" stopColor="#34d399" />
                  <stop offset="100%" stopColor="#a78bfa" />
                </linearGradient>
              </defs>

              <rect
                x="20"
                y="20"
                width="560"
                height="360"
                rx="24"
                fill="url(#g1)"
                opacity="0.08"
              />

              <g transform="translate(80,40)">
                <circle
                  cx="160"
                  cy="120"
                  r="86"
                  fill="url(#g2)"
                  opacity="0.12"
                />

                <g transform="translate(40,18)">
                  <rect
                    x="10"
                    y="90"
                    width="220"
                    height="120"
                    rx="16"
                    fill="#fff"
                    stroke="#e6e6f0"
                  />

                  <g transform="translate(26,110)">
                    <rect width="40" height="16" rx="6" fill="#eef2ff" />
                    <rect
                      x="0"
                      y="28"
                      width="180"
                      height="14"
                      rx="6"
                      fill="#fef2f2"
                    />
                    <rect
                      x="0"
                      y="52"
                      width="120"
                      height="12"
                      rx="6"
                      fill="#eef2ff"
                    />
                  </g>

                  <g transform="translate(150,24)">
                    <path
                      d="M0 0 C20 -18 70 -18 90 0"
                      fill="none"
                      stroke="#000"
                      strokeOpacity="0.06"
                      strokeWidth="10"
                      strokeLinecap="round"
                    />
                    <circle
                      cx="60"
                      cy="70"
                      r="36"
                      fill="#fff"
                      stroke="#e9e9f5"
                    />
                    <text
                      x="60"
                      y="76"
                      fontSize="24"
                      textAnchor="middle"
                      fill="#111827"
                      opacity="0.9"
                    >
                      404
                    </text>
                  </g>
                </g>
              </g>

              {/* small floating shapes */}
              <circle cx="520" cy="60" r="10" fill="#fb7185" opacity="0.22" />
              <circle cx="460" cy="340" r="14" fill="#60a5fa" opacity="0.18" />
              <rect
                x="36"
                y="320"
                width="28"
                height="8"
                rx="4"
                fill="#a78bfa"
                opacity="0.14"
              />
            </svg>
          </div>
        </section>

        {/* Right: Text + actions */}
        <section className="flex-1 text-center md:text-left">
          <h1
            id="pageTitle"
            className="text-6xl md:text-7xl font-extrabold tracking-tight text-gray-900 leading-none"
          >
            Oops.
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-sky-600 to-rose-500 ml-4">
              404
            </span>
          </h1>

          <p className="mt-4 text-lg md:text-xl text-gray-600 max-w-xl">
            Không tìm thấy trang bạn đang truy cập. Có thể đường dẫn bị sai hoặc
            trang đã bị di chuyển. Hãy thử điều hướng về trang chủ hoặc kiểm tra
            liên kết.
          </p>

          <div className="mt-8 flex flex-col sm:flex-row sm:items-center sm:justify-start gap-3">
            <button
              onClick={goHome}
              className="inline-flex items-center justify-center gap-2 px-6 py-3 rounded-lg bg-gradient-to-r from-sky-600 to-rose-500 text-white font-semibold shadow-md hover:shadow-xl transform hover:-translate-y-0.5 focus:outline-none focus:ring-4 focus:ring-sky-200"
            >
              Go home
            </button>

            <Link
              to="/contact"
              className="inline-flex items-center justify-center gap-2 px-6 py-3 rounded-lg border border-gray-200 bg-white text-gray-700 hover:bg-gray-50"
            >
              Contact support
            </Link>
          </div>

          <div className="mt-6 text-sm text-gray-500">
            <p>Or try searching for what you need:</p>
            <div className="mt-3 max-w-md">
              <label htmlFor="search404" className="sr-only">
                Search
              </label>
              <div className="flex items-center gap-2">
                <input
                  id="search404"
                  placeholder="Search our docs or help center"
                  className="flex-1 px-4 py-2 rounded-lg border border-gray-200 focus:outline-none focus:ring-2 focus:ring-sky-100"
                />
                <button className="px-4 py-2 rounded-lg bg-white border border-gray-200">
                  Search
                </button>
              </div>
            </div>
          </div>

          <p className="mt-6 text-xs text-gray-400">
            If you think this is an error, please report it so we can fix it.
          </p>
        </section>
      </main>

      <footer className="fixed bottom-6 left-1/2 transform -translate-x-1/2 text-xs text-gray-500">
        © {new Date().getFullYear()} YourCompany — All rights reserved
      </footer>
    </div>
  );
};

export default NotFoundPage;
