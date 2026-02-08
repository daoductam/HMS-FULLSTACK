import { createSlice } from "@reduxjs/toolkit";

const profileSlice = createSlice({
  name: "profile",
  initialState: {},
  reducers: {
    setprofile: (state, action) => {
      state = action.payload;
      return state;
    },
    removeprofile: (state) => {
      state = {};
      return state;
    },
  },
});

export const { removeprofile, setprofile } = profileSlice.actions;
export default profileSlice.reducer;
