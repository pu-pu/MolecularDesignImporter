import React, { Component } from 'react';
import { Button } from 'semantic-ui-react';
import { Stage, Layer, Image, Circle } from 'react-konva';

class BoxSelector extends Component {
    constructor(props) {
        super(props);
        this.state = {
            showBoxSuggestion: false,
            userDefinedBoxCorners: [],
            showContinueButton: false
        };
    }

    componentDidMount() {
        const { tempFile } = this.props;

        const image = new window.Image();
        image.src = tempFile.preview;

        image.onload = () => {
            this.setState({ image })
        }
    }

    toggleSuggestions() {
        const { showBoxSuggestion } = this.state;
        this.setState({ showBoxSuggestion: !showBoxSuggestion });
    }

    handleStageClick(e) {
        const { userDefinedBoxCorners } = this.state;
        
        if (userDefinedBoxCorners.length === 4) {
            // TODO: alert the user
            return;
        }

        const { layerX,  layerY} = e.evt;

        this.setState({
            userDefinedBoxCorners: [...userDefinedBoxCorners, { x: layerX, y:  layerY}]
        });
    }

    resetUserDefinedBoxCorners() {
        this.setState({ userDefinedBoxCorners: [] })
    }

    render() {
        const { image, userDefinedBoxCorners } = this.state;
        const { saveUserBoxSelection } = this.props;

        const circles = userDefinedBoxCorners.map(corner => {
            const { x, y } = corner;

            return (
                <Circle
                    x={x}
                    y={y}
                    radius={7}
                    fill={'black'}
                />
            )
        })

        return (
            <div>
                <p>Please click the bounding box's four corners. Selection order does not matter.</p>
                <Button onClick={() => this.resetUserDefinedBoxCorners()}>Reset</Button>
                {this.state.image && <Stage onClick={(e) => this.handleStageClick(e)} width={1.1 * image.width} height={1.1 * image.height}>
                    <Layer>
                        <Image
                            image={image}
                            opacity={0.3}
                        />
                        {circles}
                    </Layer>
                </Stage>}
                {userDefinedBoxCorners.length === 4 && <Button onClick={() => saveUserBoxSelection(userDefinedBoxCorners)}>Continue</Button>}
            </div>
        )
    }
}

export default BoxSelector;
