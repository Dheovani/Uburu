import { Component } from "react";
import { sendHttpRequest } from "../../utils/sendRequest";

class Input extends Component {
    
    constructor(props) {
        super(props);

        this.state = {
            input: '',
            title: props.title,
            placeHolder: props.placeHolder,
            repositories: [],
            selected: false
        };

        this.apiPath = "";
    }

    componentDidMount() {
        this.updateHistory();
    }

    // Busca o historico do input
    updateHistory() {
		var self = this;
        
        sendHttpRequest("GET", this.apiPath).then((responseData) => {
			self.setState({ repositories: responseData });  
		});
    }

    setContent(content) {
        this.setState({ input: content });
    }

    setSelected() {
        const selected = !this.state.selected;
        this.setState({ selected });
    }

    outClickListener(title) {
        // Fechar a sidebar quando pegar cliques do lado de fora
        document.addEventListener('click', (e) => {
            const nav = document.getElementById(title);

            // Eu sei que isso aqui tá criminoso, eu peço perdão
            const elementsExist = this.state.selected && nav;
            if (elementsExist && !nav.contains(e.target)) {
                this.setState({ selected: false })
            }
        });
    }

    render() {
        return (<></>);
    }

}

export default Input;
