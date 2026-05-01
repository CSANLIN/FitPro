import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import ForbiddenView from '@/views/error/ForbiddenView.vue'

describe('ForbiddenView', () => {
  it('应渲染 403 提示信息', () => {
    const wrapper = mount(ForbiddenView, {
      global: {
        stubs: {
          'el-button': {
            name: 'ElButton',
            template: '<button class="el-button" @click="$emit(\'click\')"><slot /></button>'
          }
        },
        mocks: {
          $router: { push: () => {} }
        }
      }
    })

    expect(wrapper.text()).toContain('403')
    expect(wrapper.text()).toContain('无权限访问')
  })

  it('应包含返回首页按钮', () => {
    const wrapper = mount(ForbiddenView, {
      global: {
        stubs: {
          'el-button': {
            name: 'ElButton',
            template: '<button class="el-button" @click="$emit(\'click\')"><slot /></button>'
          }
        },
        mocks: {
          $router: { push: () => {} }
        }
      }
    })

    const button = wrapper.find('.el-button')
    expect(button.exists()).toBe(true)
    expect(button.text()).toContain('返回首页')
  })
})
