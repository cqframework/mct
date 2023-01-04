#!/usr/bin/env bash

set -e
set -o pipefail

DIR="$( cd -P "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Login and Push to ECR
aws sts get-caller-identity

aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com
docker tag mct-frontend:$TRAVIS_COMMIT ${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/mct-frontend:${TRAVIS_COMMIT}
docker push ${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/mct-frontend:${TRAVIS_COMMIT}

# Setup kubectl context
aws eks update-kubeconfig --region us-east-1 --name aphl-eks
kube_cluster=$(aws eks describe-cluster --name aphl-eks --region us-east-1 --output=json | jq ".cluster.arn" | tr -d '"')
kubectl config use-context "$kube_cluster"

helm version
helm_chart_name="mct-frontend"
k8s_dir="${DIR}/../infrastructure/kubernetes"
aws --version

# Create namespace
if ! kubectl get namespaces | grep mct ; then
  kubectl create namespace mct
fi
 
if helm list -n mct | grep -q "$helm_chart_name"; then
  echo "Uninstalling old stack ${helm_chart_name}"
  helm upgrade "$helm_chart_name" --namespace=mct --set tag=$TRAVIS_COMMIT $k8s_dir
else
  echo "Installing new stack ${helm_chart_name}"
  helm install "$helm_chart_name" --namespace=mct --set tag=$TRAVIS_COMMIT $k8s_dir
fi

echo "Deployed!"