# 2020/10/14最新versionを取得
FROM golang:1.17.1

# アップデートとgitのインストール！！
RUN apt update && apt install git
# appディレクトリの作成
RUN mkdir /go/src/app
# ワーキングディレクトリの設定
WORKDIR /go/src/app
# ホストのファイルをコンテナの作業ディレクトリに移行
ADD . /go/src/app

RUN useradd -m --uid 1000 --groups sudo hkk \
  && echo hkk:hkk_it_kaihatu | chpasswd

# 作成したユーザーに切り替える
USER hkk


