// src/hooks.ts

import { TypedUseSelectorHook, useDispatch, useSelector } from "react-redux";
import store from "./Store"; // Import store của bạn từ Store.tsx

// Tự động suy luận ra kiểu 'RootState' và 'AppDispatch' từ store
export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

// Tạo ra các hook đã được gõ (typed) sẵn
export const useAppDispatch: () => AppDispatch = useDispatch;
export const useAppSelector: TypedUseSelectorHook<RootState> = useSelector;
