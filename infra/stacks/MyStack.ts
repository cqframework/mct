import { StackContext, Service } from "sst/constructs";

export function App({ stack }: StackContext) {
  const frontend = new Service(stack, "Frontend", {
    path: "../frontend",
    port: 3000,
  });

  stack.addOutputs({
    frontendUrl: frontend.url,
  });
}
