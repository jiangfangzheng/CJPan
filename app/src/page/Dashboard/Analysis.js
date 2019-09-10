// Analysis.js
import DPlayer from "react-dplayer";

class Example extends React.Component {
    render() {
        return (
            <DPlayer
                options={{video:{url: 'http://static.smartisanos.cn/common/video/t1-ui.mp4'}}}
            />
        )
    }
}

export default () => {
    return <div >
        <h1>Analysis Page</h1>
        <Example />
    </div>
}