import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import { BrowserRouter, Route, Switch } from "react-router-dom"

import App from './App'
import ImageUploadCard from "./Components/upload/upload.js";


const rootElement = document.getElementById('root');
ReactDOM.render(
    <BrowserRouter>
     <Switch>
        <Route exact path="/" component={App} />
        <Route exact path="/upload" component={ImageUploadCard} />
    </Switch>
    </BrowserRouter>,
    rootElement
  );