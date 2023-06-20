import { useState, useEffect } from "react";
import { Tooltip } from "./Tooltip/Tooltip";
import { DeleteMethod, GetMethod, PostMethod } from "../utils/RestMethods";
import ConfirmDialog from "./ConfirmDialog/ConfirmDialog";
import "./index.css";

interface IndexInterface {
	id?: number;
	path: string;
}

export const Index = (props: any): JSX.Element => {
	const [indexList, setIndexList] = useState<IndexInterface[]>([]);
	const [open, setOpen] = useState(false);
	const [del, setDel] = useState(false);

	const getIndex = (): void => {
		GetMethod("http://localhost:8080/api/v1/search/indices", (status: number, response: any) => {
			if (status === 200) setIndexList(response);
		});
	};

	useEffect(() => getIndex(), []);

	const addToIndex = (): void => {
		GetMethod("http://localhost:8080/api/v1/path/select", (status: number, response: any) => {
			if (status === 200) {
				PostMethod("http://localhost:8080/api/v1/search", JSON.stringify(response));
			}
		});
	};

	const deleteIndex = (): void => {
		props.setDisabled(true);
		setOpen(true);

		if (del) {
			DeleteMethod("http://localhost:8080/api/v1/search");
		}
	};

	const info = "Lista dos diretórios indexados.";

	const getName = (path: string): string | undefined => {
		const arr = path.split("\\");
		return `...\\${arr.slice(-2).join("\\")}`;
	};

	return (
		<>
			<div className="index">
				<h2>Indice {<Tooltip info={info} />}</h2>
				<div>
					{indexList.map((path: IndexInterface, i: number) => (
						<p key={i}>{getName(path.path)}</p>
					))}
				</div>
				<button disabled={props.disabled} className="add index-button" onClick={addToIndex}>
					Novo
				</button>
				<button disabled={props.disabled} className="delete-index index-button" onClick={deleteIndex}>
					Exluir indice
				</button>
			</div>
			{open && (
				<ConfirmDialog
					title="Tem certeza de que quer excluir?"
					text="Essa ação apagará o indice inteiro."
					setOpen={setOpen}
					setDel={setDel}
					setDisabled={props.setDisabled}
				/>
			)}
		</>
	);
};
