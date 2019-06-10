import React, { Component } from "react";
import "./App.css";
import FixedMenuLayout from "./FixedMenuLayout";
import { BrowserRouter } from "react-router-dom";

class App extends Component {
  render() {
    return (
      <BrowserRouter>
        <FixedMenuLayout />
      </BrowserRouter>
    );
  }
}

export default App;
