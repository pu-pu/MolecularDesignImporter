import axios from 'axios';
import Constants from '../Constants';

class ImageProcessingService {
    sendImage = (file, corners) => {
        const data = new FormData();
        data.append("file", file);
        data.append("height", 30);
        data.append("width", 34);

        return axios
            .post(`${Constants.API_ENDPOINT}/upload`, data, {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })
            .then(res => res.data);
            // TODO handle non 200 case
    }
}

export default ImageProcessingService;
