import React, { Component } from "react";
import AtomLegendItem from "./AtomLegendItem";

class AtomLegend extends Component {
  render() {
    const { atomLegendItems, updateLegendItem } = this.props;

    const atomLabels = Array.from(atomLegendItems).map(atomLabelData => {
      const atomLabel = atomLabelData[1];
      return (
        <React.Fragment>
          <AtomLegendItem
            atomLabel={atomLabel}
            updateLegendItem={(atomLabel, text) => updateLegendItem(atomLabel, text)}
          />
        </React.Fragment>
      );
    })

    return atomLabels;
  }
}

export default AtomLegend;
