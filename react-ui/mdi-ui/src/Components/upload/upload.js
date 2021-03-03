import React from "react";
import Card from "@material-ui/core/Card";
import CardActions from '@material-ui/core/CardActions';
import CardActionArea from '@material-ui/core/CardActionArea';
import Button from '@material-ui/core/Button';
import Typography from '@material-ui/core/Typography';
import AddPhotoAlternateIcon from '@material-ui/icons/AddPhotoAlternate';
import IconButton from '@material-ui/core/IconButton';
import CardContent from '@material-ui/core/CardContent';
import {Link } from "react-router-dom";
import ReactCrop, { makeAspectCrop } from 'react-image-crop'
import 'react-image-crop/dist/ReactCrop.css'

class ImageUploadCard extends React.Component {
    state = {
      mainState: "initial", 
      imageUploaded: 0,
      imageEdit: 0,
      selectedFile: null,
      crop:{
          x:10,
          y:10,
          width:80,
          height:80,
      }
    };

    handleUploadClick = event => {
        console.log();
        var file = event.target.files[0];
        const reader = new FileReader();
        var url = reader.readAsDataURL(file);
    
        reader.onloadend = function(e) {
          this.setState({
            selectedFile: [reader.result]
          });
        }.bind(this);
        console.log(url); // Would see a path?
    
        this.setState({
          mainState: "uploaded",
          selectedFile: event.target.files[0],
          imageUploaded: 1
        });
    };

    handleEditClick = event => {
        console.log();
    
        this.setState({
          mainState: "edit",
          imageEdit: 1
        });
    };

    handleSubmitClick = event => {
      console.log();
  
      this.setState({
        mainState: "edit",
        imageEdit: 1
      });
  };

    onCropComplete = crop => {
        console.log('onCropComplete', crop)
    }
    
    onCropChange = crop => {
        this.setState({ crop })
    }

    renderInitialState() {
        const { classes, theme } = this.props;
        const { value } = this.state;
    
        return (
            <div>
         <h1>Molecular Design Importer</h1>
         <h2>Upload an Image</h2>
          <Card className={300} variant="outlined">
            <CardContent>
            <input
            ref = {(fileUpload) => {this.fileUpload = fileUpload;}}
                  accept="image/*"
                  id="upload-icon"
                  multiple
                  type="file"
                  onChange={this.handleUploadClick}
                  style={{visibility: 'hidden'}}
                />
                <label htmlFor="upload-icon">
                <Button onClick={() => this.fileUpload.click()} variant="contained" color="primary">
                Upload Image
                </Button>
                </label>
            </CardContent>
          </Card>
          </div>
        );
    }
    
    renderUploadedState() {
        const { classes, theme } = this.props;
    
        return (
            <div>
         <h1>Molecular Design Importer</h1>
         <h2>Upload an Image</h2>
          <Card>
              <CardContent>
                  <img
                    width="30%"
                    src={this.state.selectedFile}
                    />
                <input
            ref = {(fileUpload) => {this.fileUpload = fileUpload;}}
                  accept="image/*"
                  id="upload-icon"
                  multiple
                  type="file"
                  onChange={this.handleUploadClick}
                  style={{visibility: 'hidden'}}
                />
                <label htmlFor="upload-icon">
                <Button onClick={() => this.fileUpload.click()} variant="contained" color="secondary">
                Re-Upload
                </Button>
                </label>
                <Button onClick={() => this.handleEditClick()} variant="contained" color="primary">
                Edit Image
                </Button>
              </CardContent> 
          </Card>
          </div>
        );
    }

    renderEditState() {
        const { classes, theme } = this.props;
    
        return (
            <div>
         <h1>Molecular Design Importer</h1>
         <h2>Set Bounding Box</h2>
          <Card>
              <CardContent>
              {this.state.selectedFile && (
                <ReactCrop
                    src={this.state.selectedFile}
                    crop={this.state.crop}
                    onComplete={this.onCropComplete}
                    onChange={this.onCropChange}
                />
        )}
                <Button variant="contained" color="primary">
                Submit
                </Button>
              </CardContent> 
          </Card>
          </div>
        );
    }

    render() {
        const { classes, theme } = this.props;
    
        return (
          <React.Fragment>
            <div>
                {(this.state.mainState == "initial" && this.renderInitialState()) ||
                (this.state.mainState == "uploaded" && this.renderUploadedState()) ||
                (this.state.mainState == "edit" && this.renderEditState())}
            </div>
          </React.Fragment>
        );
    }
}

export default ImageUploadCard;