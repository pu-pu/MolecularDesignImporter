import React from "react";
import BoxSelector from "./BoxSelector";

const BoxSelectionComponent = ({ file, saveUserBoxSelection }) => (
  <div>
    <BoxSelector
      tempFile={file}
      saveUserBoxSelection={saveUserBoxSelection}
    />
  </div>
);

export default BoxSelectionComponent;
