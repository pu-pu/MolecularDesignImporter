import React from 'react';

import { Header, Image } from 'semantic-ui-react'

const FileDetails = ({ file }) => (
    <div>
        <Header as="h3">File Details</Header>
        {file.name}
        <Image size="large" src={file.preview} />
    </div>
);

export default FileDetails;
