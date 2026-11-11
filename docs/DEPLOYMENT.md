# 배포 서버 구성

## 구성

```
브라우저 ──HTTPS(443)──▶ nginx (EC2 호스트) ──HTTP──▶ app 컨테이너 (127.0.0.1:8080) ──▶ RDS MySQL 8
```

| 항목 | 값 |
|---|---|
| 도메인 | `sopt39collaboration.kro.kr` |
| 서버 | AWS EC2 t3.micro (Ubuntu 24.04, ap-northeast-2), 탄력적 IP |
| DB | AWS RDS MySQL 8 (퍼블릭 액세스 없음, EC2 보안 그룹에서만 3306 허용) |
| 리버스 프록시 | nginx (호스트 설치) |
| 인증서 | ZeroSSL (ACME), certbot `--nginx` 플러그인 (자동 갱신) |
| 앱 | Docker 컨테이너, `cd.yml` 이 배포. 8080은 `127.0.0.1` 에만 바인딩 |

## EC2 보안 그룹 인바운드

| 포트 | 소스 | 용도 |
|---|---|---|
| 22 | `0.0.0.0/0` | SSH (GitHub Actions IP가 고정되지 않음) |
| 80 | `0.0.0.0/0` | 인증서 발급·갱신, HTTPS 리다이렉트 |
| 443 | `0.0.0.0/0` | HTTPS |

8080은 외부에 열지 않는다. 앱은 nginx를 통해서만 접근한다.

## 최초 세팅 (1회)

전제: 도메인 A 레코드가 EC2 탄력적 IP를 가리키고, 보안 그룹에서 80·443이 열려 있다.

```bash
# 1. nginx, certbot 설치
sudo apt update
sudo apt install -y nginx certbot python3-certbot-nginx

# 2. 레포의 infra/nginx/collaboration.conf 내용을 서버에 배치
sudo vi /etc/nginx/sites-available/collaboration
sudo ln -s /etc/nginx/sites-available/collaboration /etc/nginx/sites-enabled/
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t && sudo systemctl reload nginx

# 3. ZeroSSL EAB 자격 증명 발급 (응답의 eab_kid, eab_hmac_key 사용)
curl -s https://api.zerossl.com/acme/eab-credentials-email --data "email={이메일}"

# 4. 인증서 발급 (443 블록·리다이렉트 자동 추가)
KID="{eab_kid}"
HMAC="{eab_hmac_key}"
sudo certbot --nginx --server https://acme.zerossl.com/v2/DV90 --eab-kid "$KID" --eab-hmac-key "$HMAC" -d sopt39collaboration.kro.kr --redirect

# 5. 자동 갱신 확인 (발급 서버 설정은 certbot이 기억한다)
sudo certbot renew --dry-run
```

### Let's Encrypt를 쓰지 않는 이유

`kro.kr` 은 공개 접미사 목록(PSL)에 없어 모든 `*.kro.kr` 사용자가
Let's Encrypt의 "등록 도메인당 주 50개" 한도를 공유한다.
실제로 `too many certificates (50) already issued for "kro.kr"` 로 발급이 거부되어 ZeroSSL을 사용한다.

EAB 키(`eab_hmac_key`)는 비밀값이므로 레포·메신저에 남기지 않는다.

## 환경변수 (GitHub Secrets)

`docs/ARCHITECTURE.md` 의 CI/CD 절 참조.
