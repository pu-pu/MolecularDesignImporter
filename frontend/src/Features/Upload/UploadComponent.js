import React, { Component } from 'react';

import Dropzone from 'react-dropzone';

class UploadComponent extends Component {
    handleFileUpload(acceptedFiles, rejectedFiles) {
        // TODO: assert correct files only
        this.props.handleFileUpload(acceptedFiles[0])
    }

    render() {
        return (
            <div className="upload-component">
                <div className="upload-box">
                    <Dropzone
                        className="upload-dropzone"
                        onDrop={(acceptedFiles, rejectedFiles) => this.handleFileUpload(acceptedFiles, rejectedFiles)}
                        accept={"image/jpeg, image/png"}>
                        <div className="upload-dropzone-request-container">
                            <div className="upload-dropzone-request">
                                Please click here or drag to upload your image
                            </div>
                        </div>
                    </Dropzone>
                </div>
            </div>
        )
    }
}

export default UploadComponent;
