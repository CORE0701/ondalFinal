
function closeDaumPostcode() {
	// iframe을 넣은 element를 안보이게 한다.
	var element_layer = document.getElementById('iframe-layer');
	element_layer.style.display = 'none';
}

function regUserAddress() {
	var element_layer = document.getElementById('iframe-layer');
	
	new daum.Postcode({
		oncomplete: function(data) {
			const address = data.roadAddress || data.jibunAddress;

			// 우편번호와 주소 정보를 해당 필드에 넣는다.
			document.getElementById("userAddress").value = address;
			
			// 커서를 상세주소 필드로 이동한다.
			document.getElementById("detailAddress").focus();

			// iframe을 넣은 element를 안보이게 한다.
			// (autoClose:false 기능을 이용한다면, 아래 코드를 제거해야 화면에서 사라지지 않는다.)
			element_layer.style.display = 'none';
			
			fetch(`https://dapi.kakao.com/v2/local/search/address.json?query=${encodeURIComponent(address)}`, {
					headers: {
						Authorization: 'KakaoAK c7dd39e36776f90fb259bbd8ac3fcdc6'
					}
			})
					.then(response => response.json())
					.then(result => {
						console.log('Kakao 응답:', result);
						const location = result.documents[0];
						if (location) {
							document.getElementById('userAddresslatitude').value = parseFloat(location.y).toFixed(6);
							document.getElementById('userAddresslongitude').value = parseFloat(location.x).toFixed(6);
						} else {
							alert('주소의 위치 정보를 찾을 수 없습니다.');
						}
					})
					.catch(error => {
						console.error('좌표 변환 에러:', error);
					});
			},
			width: '100%',
			height: '100%',
			maxSuggestItems: 5
		}).embed(element_layer);
	
		// iframe을 넣은 element를 보이게 한다.
		element_layer.style.display = 'block';
	
		// iframe을 넣은 element의 위치를 화면의 가운데로 이동시킨다.
		initLayerPosition();
}

// 브라우저의 크기 변경에 따라 레이어를 가운데로 이동시키고자 하실때에는
// resize이벤트나, orientationchange이벤트를 이용하여 값이 변경될때마다 아래 함수를 실행 시켜 주시거나,
// 직접 element_layer의 top,left값을 수정해 주시면 됩니다.
function initLayerPosition() {
	var element_layer = document.getElementById('iframe-layer');
	var width = 300; //우편번호서비스가 들어갈 element의 width
	var height = 400; //우편번호서비스가 들어갈 element의 height
	var borderWidth = 5; //샘플에서 사용하는 border의 두께

	// 위에서 선언한 값들을 실제 element에 넣는다.
	element_layer.style.width = width + 'px';
	element_layer.style.height = height + 'px';
	
	element_layer.style.border = borderWidth + 'px solid';
	// 실행되는 순간의 화면 너비와 높이 값을 가져와서 중앙에 뜰 수 있도록 위치를 계산한다.
	//element_layer.style.left = (((window.innerWidth || document.documentElement.clientWidth) - width) / 2 - borderWidth) + 'px';
	//element_layer.style.top = (((window.innerHeight || document.documentElement.clientHeight) - height) / 2 - borderWidth) + 'px';
}

function sample6_execDaumPostcode() {
	new daum.Postcode({
		oncomplete: function(data) {
			// 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

			// 각 주소의 노출 규칙에 따라 주소를 조합한다.
			// 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
			var addr = ''; // 주소 변수
			var extraAddr = ''; // 참고항목 변수

			//사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
			if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
				addr = data.roadAddress;
			} else { // 사용자가 지번 주소를 선택했을 경우(J)
				addr = data.jibunAddress;
			}

			// 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
			if (data.userSelectedType === 'R') {
				// 법정동명이 있을 경우 추가한다. (법정리는 제외)
				// 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
				if (data.bname !== '' && /[동|로|가]$/g.test(data.bname)) {
					extraAddr += data.bname;
				}
				// 건물명이 있고, 공동주택일 경우 추가한다.
				if (data.buildingName !== '' && data.apartment === 'Y') {
					extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
				}
				// 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
				if (extraAddr !== '') {
					extraAddr = ' (' + extraAddr + ')';
				}
				// 조합된 참고항목을 해당 필드에 넣는다.
				document.getElementById("sample6_extraAddress").value = extraAddr;

			} else {
				document.getElementById("sample6_extraAddress").value = '';
			}

			// 우편번호와 주소 정보를 해당 필드에 넣는다.
			document.getElementById('sample6_postcode').value = data.zonecode;
			document.getElementById("sample6_address").value = addr;
			// 커서를 상세주소 필드로 이동한다.
			document.getElementById("sample6_detailAddress").focus();
		}
	}).open();
}

function execDaumPostcode() {
	new daum.Postcode({
		oncomplete: function(data) {
			const address = data.roadAddress || data.jibunAddress;
			console.log('선택된 주소:', address);
			document.getElementById('riderHubAddress').value = address;

			// 주소로 좌표 요청
			fetch(`https://dapi.kakao.com/v2/local/search/address.json?query=${encodeURIComponent(address)}`, {
				headers: {
					Authorization: 'KakaoAK c7dd39e36776f90fb259bbd8ac3fcdc6'
				}
			})
				.then(response => response.json())
				.then(result => {
					console.log('Kakao 응답:', result);
					const location = result.documents[0];
					if (location) {
						document.getElementById('hubAddressLatitude').value = parseFloat(location.y).toFixed(6);
						document.getElementById('hubAddressLongitude').value = parseFloat(location.x).toFixed(6);
					} else {
						alert('주소의 위치 정보를 찾을 수 없습니다.');
					}
				})
				.catch(error => {
					console.error('좌표 변환 에러:', error);
				});
		}
	}).open();
}
//버튼 클릭 시 선택된 버튼에 'selected' 클래스 추가하고, 선택된 배달 범위를 hidden input에 설정
function selectRange(button, range) {
	// 모든 버튼에서 'selected' 클래스 제거
	const allButtons = document.querySelectorAll('.delivery-range-btn');
	allButtons.forEach(btn => {
		btn.classList.remove('selected');
	});

	// 클릭된 버튼에 'selected' 클래스 추가
	button.classList.add('selected');

	// hidden input에 선택된 배달 범위 값 저장
	document.getElementById('deliveryRange').value = range;

	// 선택된 값(배달 범위) 콘솔로 출력
	console.log('Selected delivery range:', range);
}
