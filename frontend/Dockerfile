# build phase
FROM --platform=linux/amd64 node:18.12.1-alpine as builder

RUN mkdir /app

WORKDIR /app

COPY package.json \
  yarn.lock \
  ./

RUN yarn install --frozen-lockfile

COPY . ./

RUN yarn build

# production environment
FROM --platform=linux/amd64 nginx:1.23.3-alpine

COPY --from=builder /app/build /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]
