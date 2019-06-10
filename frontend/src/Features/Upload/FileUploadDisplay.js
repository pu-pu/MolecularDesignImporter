import React from 'react';

import FileDetails from './FileDetails';

const FileUploadDisplay = ({ file }) => (
    <div>
        <FileDetails file={file} />
    </div>
);

export default FileUploadDisplay;