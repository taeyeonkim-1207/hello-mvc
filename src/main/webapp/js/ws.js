/**
 * ws 연결요청
 * - http통신 요청후 ws으로 프로토콜 업그레이드
 */
const ws = new WebSocket(`ws://${location.host}/mvc/helloWebSocket`); 

ws.addEventListener('open', (e) => console.log('open : ', e));
ws.addEventListener('error', (e) => console.log('error : ', e));
ws.addEventListener('close', (e) => console.log('close : ', e));
ws.addEventListener('message', (e) => {
	console.log('message : ', e);
	const {messageType, data : {msg}, time} = JSON.parse(e.data);
	switch(messageType){
		case 'NOTIFY_NEW_COMMENT' : 
			const wrapper = document.querySelector("#notification");
			const i = document.createElement('i');
			i.classList.add('fa-solid', 'fa-bell', 'bell');
			i.addEventListener('click', () => {
				alert(msg);
				i.remove();
			});
			wrapper.append(i);
			break;
	}
	
		
});