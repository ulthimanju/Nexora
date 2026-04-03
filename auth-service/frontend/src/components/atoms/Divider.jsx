import { cn } from '../../lib/cn'

export const Divider = ({ text, className }) => {
  if (!text) {
    return <hr className={cn('border-border my-5', className)} />
  }

  return (
    <div className={cn('relative my-5', className)}>
      <div className="absolute inset-0 flex items-center">
        <div className="w-full border-t border-border"></div>
      </div>
      <div className="relative flex justify-center text-sm">
        <span className="bg-background px-3 text-secondary">{text}</span>
      </div>
    </div>
  )
}
