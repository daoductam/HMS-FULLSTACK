import { configureStore } from "@reduxjs/toolkit";
import jwtReducer from "./Slices/JwtSlice";
import userReducer from "./Slices/UserSlice";

// export default configureStore({
//   reducer: {
//     jwt: jwtReducer,
//     user: userReducer,
//   },
// });

// 1. GÁN store vào một biến
const store = configureStore({
  reducer: {
    jwt: jwtReducer,
    user: userReducer,
  },
});

// 2. ĐỊNH NGHĨA VÀ EXPORT CÁC TYPE
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

// 3. EXPORT store
export default store;
