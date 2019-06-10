import React, { Component } from "react";

import { Button, Input, Form } from "semantic-ui-react";

class DownloadComponent extends Component {
  constructor(props) {
      super(props);

      this.state = {
        structureName: 'Example name',
        angle: 135,
      };
  }

  download() {
    const { structureName, a, b, c, angle } = this.state;

    if (angle == null || a == null || b == null || c == null || !Number(angle) || !Number(a) || !Number(b) || !Number(c)) {
        alert('Please specify a, b and c length');
        return;
    }

    const { download } = this.props;

    download(structureName, angle, a, b, c);
  }

  render() {
    return (
      <div>
        <Form>
          <Form.Field>
            <label>Structure name</label>
            <Input
              type="text"
              onChange={e => this.setState({ structureName: e.target.value })}
              value={this.state.structureName}
              placeholder="Structure name"
            />
          </Form.Field>
          <Form.Field>
            <label>Cell length a</label>
            <Input
              type="number"
              onChange={e => this.setState({ a: e.target.value })}
              value={this.state.a}
              placeholder="Cell length a"
            />
          </Form.Field>
          <Form.Field>
            <label>Cell length b</label>
            <Input
              type="number"
              onChange={e => this.setState({ b: e.target.value })}
              value={this.state.b}
              placeholder="Cell length b"
            />
          </Form.Field>
          <Form.Field>
            <label>Cell length c</label>
            <Input
              type="number"
              onChange={e => this.setState({ c: e.target.value })}
              value={this.state.c}
              placeholder="Cell length c"
            />
          </Form.Field>
          <Form.Field>
            <label>Box angle</label>
            <Input
              type="number"
              onChange={e => this.setState({ angle: e.target.value })}
              value={this.state.angle}
              placeholder="Box angle"
            />
          </Form.Field>
        </Form>
        <Button onClick={() => this.download()}>Download .cif file</Button>
      </div>
    );
  }
}

export default DownloadComponent;
