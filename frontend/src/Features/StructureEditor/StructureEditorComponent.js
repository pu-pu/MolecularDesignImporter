import React, { Component } from "react";
import { withRouter } from "react-router-dom";

import { Button } from "semantic-ui-react";

import { Stage, Layer, Line } from "react-konva";

import Atom from "./Atom";
import AtomLegend from "./AtomLegend";

import { EditorModes } from '../../FixedMenuLayout';

class StructureEditorComponent extends Component {
  constructor(props) {
    super(props);

    const bounds = this.getBounds();

    this.state = {
      bounds,
      mode: EditorModes.NORMAL,
    };
  }

  componentDidMount() {
    const atomLegendItems = new Map();

    this.getAtomTypes().forEach((item, index) => {
      atomLegendItems.set(item.colour, {
        x: 50,
        y: index * 25 + 25,
        colour: item.colour,
        label: item.label,
      });
    })

    this.setState({ atomLegendItems });
  }

  getBounds() {
    const { structureData } = this.props;
    const { atoms, box } = structureData;

    const upperX = Math.max(
      ...box.corners.map(corner => corner.x),
      ...atoms.map(atom => atom.x)
    );
    const upperY = Math.max(
      ...box.corners.map(corner => corner.y),
      ...atoms.map(atom => atom.y)
    );

    return { upperX, upperY };
  }

  getAtoms() {
    const { structureData } = this.props;
    const { atoms } = structureData;

    return atoms.map(atom => {
      return (
        <Atom
          key={atom.id}
          id={atom.id}
          x={atom.x}
          y={atom.y}
          radius={8}
          colour={atom.outside ? 'black' : atom.colourString}
          representative={atom.representative}
          onClick={this.determineOnClick()}
        />
      );
    });
  }

  determineOnClick() {
    switch (this.state.mode) {
      case (EditorModes.NORMAL):
        return () => {}; // no-op
      case (EditorModes.DELETE):
        return (id) => this.props.deleteAtom(id);
    }
  }


  isCornerInAtom(corner, atom) {
    return (
      Math.pow(
        Math.pow(corner.x - atom.x, 2) + Math.pow(corner.y - atom.y, 2),
        0.5
      ) < atom.radius
    );
  }

  getBoxLines() {
    const { structureData } = this.props;
    const { box, atoms } = structureData;

    box.corners.forEach(corner => {
      // atoms.forEach(atom => {
      //   if (this.isCornerInAtom(corner, atom)) {
      //     corner.x = atom.x;
      //     corner.y = atom.y;
      //   }
      // });
    });

    return Array.from(Array(4).keys()).map(i => {
      return (
        <Line
          points={[
            box.corners[i].x,
            box.corners[i].y,
            box.corners[(i + 1) % 4].x,
            box.corners[(i + 1) % 4].y
          ]}
          closed
          stroke="black"
        />
      );
    });
  }

  getAtomTypes() {
    const { structureData } = this.props;
    const { atoms } = structureData;

    const types = [];

    atoms.forEach(atom => {
      if (
        types.filter(item => item.colour === atom.colourString).length === 0
      ) {
        types.push({
          colour: atom.colourString,
          label: atom.label
        });
      }
    });

    return types;
  }

  toggleMode(modeToToggle) {
    const mode = (this.state.mode === modeToToggle)
      ? EditorModes.NORMAL
      : modeToToggle;

    this.setState({ mode });

  }

  updateLegendItem(atomLabel, text) {
    const { atomLegendItems } = this.state;

    const existingItem = atomLegendItems.get(atomLabel.colour);
    existingItem.label = text;

    this.setState({ atomLegendItems });
  }

  render() {
    if (!this.state.atomLegendItems) {
      return <div />;
    }

    return (
      <div>
        <div>
          <Button 
            onClick={() => this.toggleMode(EditorModes.DELETE)}
            color={this.state.mode === EditorModes.DELETE ? ' green' : ''}
          >
            Delete Mode
          </Button>
        </div>
        <div className="structure-editor">
          <Stage width={150} height={this.state.bounds.upperY}>
            <Layer>
              <AtomLegend
                atomLegendItems={this.state.atomLegendItems}
                updateLegendItem={(atomLabel, text) =>
                  this.updateLegendItem(atomLabel, text)
                }
              />
            </Layer>
          </Stage>
          <Stage
            width={this.state.bounds.upperX + 50}
            height={this.state.bounds.upperY + 20}
          >
            <Layer>
              {this.getAtoms()}
              {this.getBoxLines()}
            </Layer>
          </Stage>
        </div>
        <Button onClick={() => this.props.finishEditing(this.state.atomLegendItems)}>Continue</Button>
      </div>
    );
  }
}

export default withRouter(StructureEditorComponent);
