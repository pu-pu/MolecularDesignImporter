import React, { Component } from "react";
import { Container, Menu } from "semantic-ui-react";
import { Route, Switch, Link, withRouter } from "react-router-dom";

import axios from "axios";
import uuidv4 from "uuid/v4";

import Constants from "./Constants";

import Upload from "./Features/Upload/UploadComponent";
import BoxSelectionComponent from "./Features/BoxSelection/BoxSelectionComponent";
import StructureEditorComponent from "./Features/StructureEditor/StructureEditorComponent";
import DownloadComponent from "./Features/Download/DownloadComponent";

import ImageProcessingService from "./Services/ImageProcessingService";

export const EditorModes = Object.freeze({
  NORMAL: 1,
  DELETE: 2,
});

class FixedMenuLayout extends Component {
  constructor(props) {
    super(props);

    this.imageProcessingService = new ImageProcessingService();

    this.state = {
      atoms: [],
      box: {
        width: 0,
        height: 0
      }
    };
  }

  deleteAtom(id) {
    this.setState({
      structureData: {
        ...this.state.structureData,
        atoms: this.state.structureData.atoms.filter(atom => atom.id !== id)
      }
    });
  }

  // new school below
  handleFileUpload(file) {
    this.setState({ file }, () => {
      this.props.history.push("/box");
    });
  }

  async saveUserBoxSelection(corners) {
    const { file } = this.state;

    let structureData;
    try {
      structureData = await this.imageProcessingService.sendImage(file);
    } catch (e) {
      alert('Sorry, we had trouble connecting to the server.')
      return;
    }

    const markedStructureData = await axios
      .post(`${Constants.API_ENDPOINT}/exclusion`, {
        atoms: structureData.atoms,
        corners
      })
      .then(res => res.data);

    this.setState({ structureData: markedStructureData, userDefinedBox: markedStructureData.corners }, () => {
      this.props.history.push('/editor');
    })
  }

  finishEditing(atomLegendItems) {
    const { structureData } = this.state;
    const { atoms } = structureData;

    const mapping = {};
    atomLegendItems.forEach(item => {
      mapping[item.colour] = item.label;
    });

    atoms.forEach(atom => {
      atom.label = mapping[atom.colourString];
    });

    this.setState(
      {
        structureData: {
          ...this.state.structureData,
          atoms
        }
      },
      () => this.props.history.push("/download")
    );
  }

  download(structureName, angle, a, b, c) {
    axios
      .post(`${Constants.API_ENDPOINT}/files`, {
        atoms: this.state.structureData.atoms,
        name: structureName,
        angle,
        a,
        b,
        c,
        corners: this.state.structureData.box.corners,
      })
      .then(res => {
        const fileAsBlob = new Blob([res.data], { type: "text/plain" });
        const fileUrl = URL.createObjectURL(fileAsBlob);

        const downloadLink = document.createElement("a");
        document.body.appendChild(downloadLink);
        downloadLink.download = `${uuidv4()}.cif`;
        downloadLink.href = fileUrl;
        downloadLink.click();
        document.body.removeChild(downloadLink);
      });
  }

  render() {
    return (
      <div>
        <Menu fixed="top" inverted>
          <Container>
            <Menu.Item as="a" header>
              <Link to={"/"}>Molecular Design Importer</Link>
            </Menu.Item>
          </Container>
        </Menu>

        <Container text style={{ marginTop: "7em" }}>
          <Switch>
            <Route
              path={"/"}
              exact={true}
              render={() => (
                <Upload
                  handleFileUpload={file => this.handleFileUpload(file)}
                />
              )}
            />
            <Route
              path={"/box"}
              exact={true}
              render={() => (
                <BoxSelectionComponent
                  file={this.state.file}
                  saveUserBoxSelection={corners =>
                    this.saveUserBoxSelection(corners)
                  }
                />
              )}
            />
            <Route
              path={"/editor"}
              exact={true}
              render={() => (
                <StructureEditorComponent
                  corners={this.state.userDefinedBox}
                  structureData={this.state.structureData}
                  finishEditing={atomLegendItems =>
                    this.finishEditing(atomLegendItems)
                  }
                  deleteAtom={id => this.deleteAtom(id)}
                />
              )}
            />
            <Route
              path={"/download"}
              exact={true}
              render={() => (
                <DownloadComponent
                  corners={this.state.userDefinedBox}
                  structureData={this.state.structureData}
                  download={(structureName, angle, a, b, c) => this.download(structureName, angle, a, b, c)}
                />
              )}
            />
          </Switch>
        </Container>
      </div>
    );
  }
}

export default withRouter(FixedMenuLayout);
