import React, { Fragment } from "react";
import { Circle, Text } from "react-konva";

const Atom = ({debug, x, y, id, radius, colour, representative, onClick }) => {
  return(
      <Fragment>
          {debug && <Text x={x + 2} y={y + 5} text={id} />}
          <Circle
            x={x}
            y={y}
            radius={representative ? 1.5 * radius : radius}
            fill={colour}
            onClick={() => onClick(id)}
          />
      </Fragment>

  )

}
export default Atom;
