# build phase
FROM --platform=linux/amd64 node:18.12.1-alpine as builder


ENV BASE_URL="http://a913da9435453489fb73187d9b16edc5-1641411053.us-east-1.elb.amazonaws.com"

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
