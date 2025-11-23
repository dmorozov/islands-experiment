declare module 'react-dom' {
  export * from 'react';
  import { VNode, ComponentChild } from 'preact';

  export function render(
    vnode: ComponentChild,
    parent: Element | Document | ShadowRoot | DocumentFragment,
    replaceNode?: Element | Text
  ): void;

  export function hydrate(
    vnode: ComponentChild,
    parent: Element | Document | ShadowRoot | DocumentFragment,
    replaceNode?: Element | Text
  ): void;

  export function createPortal(
    vnode: ComponentChild,
    container: Element,
    key?: string
  ): VNode<any>;

  export function unmountComponentAtNode(container: Element): boolean;
  export function findDOMNode(component: any): Element | null;

  export const version: string;
  export const unstable_batchedUpdates: (callback: () => void) => void;
  export function flushSync<R>(fn: () => R): R;
}

declare module 'react-dom/client' {
  import { ComponentChild } from 'preact';

  export interface Root {
    render(children: ComponentChild): void;
    unmount(): void;
  }

  export interface RootOptions {
    onRecoverableError?: (error: any) => void;
    identifierPrefix?: string;
  }

  export function createRoot(
    container: Element | DocumentFragment,
    options?: RootOptions
  ): Root;

  export function hydrateRoot(
    container: Element | Document,
    children: ComponentChild,
    options?: RootOptions
  ): Root;
}

declare module 'react-dom/server' {
  import { VNode } from 'preact';

  export function renderToString(vnode: VNode): string;
  export function renderToStaticMarkup(vnode: VNode): string;
}
