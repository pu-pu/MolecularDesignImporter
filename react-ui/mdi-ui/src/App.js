import React, {Component} from 'react'
import Button from '@material-ui/core/Button'
import {Link } from "react-router-dom";

class App extends Component {

  constructor(props){
    super(props);
    this.state={
      appName: "Molecular Design Importer"
    }
  }

  render() {
    return (
      <div className="AppHome">
        <h1>Molecular Design Importer</h1>
        <h2>Home</h2>
        <body>Application Description</body>
        <Button variant="contained" color="primary" component={Link} to="/upload">
          Get Started
        </Button>
      </div>
    )
  }
}

export default App