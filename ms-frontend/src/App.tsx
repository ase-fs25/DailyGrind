import { Routes, Route } from "react-router-dom";

import "./styles/app.css";

import Login from "./components/login/Login";
import Registration from "./components/login/Registration";
import Feed from "./components/screens/Feed";
import Posts from "./components/screens/Posts";
import Friends from "./components/screens/Friends";

function App() {
  return (
    <div className="app">
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/registration" element={<Registration />} />
        <Route path="/feed" element={<Feed />} />
        <Route path="/posts" element={<Posts />} />
        <Route path="/friends" element={<Friends />} />
      </Routes>
    </div>
  );
}

export default App;
