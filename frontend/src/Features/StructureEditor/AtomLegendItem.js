import React, { Component } from 'react';
import Atom from "./Atom";
import { Text } from "react-konva";

class AtomLegendItem extends Component {
    updateText() {
        const { atomLabel } = this.props;

        const text = prompt("Please input the atom's label");

        if (!text) {
            return;
        }

        this.props.updateLegendItem(atomLabel, text);
    }

    render() {
        const { atomLabel } = this.props;

        return (
            <React.Fragment>
                <Atom
                    x={atomLabel.x}
                    y={atomLabel.y}
                    radius={8}
                    colour={atomLabel.colour}
                />
                <Text
                    text={atomLabel.label}
                    x={atomLabel.x + 13}
                    y={atomLabel.y - 5}
                    onClick={() => this.updateText()}
                />
            </React.Fragment>
        )
    }
}

export default AtomLegendItem;
